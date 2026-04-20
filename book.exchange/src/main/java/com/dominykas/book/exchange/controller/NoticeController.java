package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.noticeDTO.PageResponseDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeFilterDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.service.NoticeService;
import com.dominykas.book.exchange.service.PublicationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;
    private final PublicationService publicationService;
    private final ObjectMapper mapper;

    @PostMapping
    public NoticeResponseDTO createNotice(@RequestBody NoticeRequestDTO noticeRequestDTO) {
        return noticeService.createNotice(noticeRequestDTO);
    }

    @GetMapping
    public PageResponseDTO<NoticeResponseDTO> getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "timePosted") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long posterId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String language,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseYearFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseYearTo,
            @RequestParam(required = false) Integer minPageCount,
            @RequestParam(required = false) Integer maxPageCount,
            @RequestParam(required = false) String cover,
            @RequestParam(required = false) Boolean colored,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate postedFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate postedTo
    ) {
        NoticeFilterDTO filter = new NoticeFilterDTO();
        filter.setPosterId(posterId);
        filter.setTitle(title);
        filter.setAuthor(author);
        filter.setLanguage(language);
        filter.setReleaseYearFrom(releaseYearFrom);
        filter.setReleaseYearTo(releaseYearTo);
        filter.setMinPageCount(minPageCount);
        filter.setMaxPageCount(maxPageCount);
        filter.setCover(cover);
        filter.setColored(colored);
        filter.setPostedFrom(postedFrom);
        filter.setPostedTo(postedTo);

        return noticeService.getNotices(page, size, sortBy, sortDir, filter);
    }

    @GetMapping("/{id}")
    public NoticeResponseDTO getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    @GetMapping("/publication/{publicationId}")
    public NoticeResponseDTO getNoticeByPublicationId(@PathVariable Long publicationId) {
        return noticeService.getNoticeByPublicationId(publicationId);
    }

    @GetMapping("/poster/{posterId}")
    public List<NoticeResponseDTO> getNoticesByPosterId(@PathVariable Long posterId) {
        return noticeService.getNoticesByPosterId(posterId);
    }

    @PostMapping("/seed")
    public String seed() throws Exception {

        InputStream is = new ClassPathResource("eval/books.json").getInputStream();

        List<PublicationRequestDTO> books =
                mapper.readValue(is, new TypeReference<List<PublicationRequestDTO>>() {});

        log.info("Starting seeding of {} books", books.size());

        AtomicInteger count = new AtomicInteger(0);

        ExecutorService seedExecutor = Executors.newFixedThreadPool(6);

        books.forEach(book ->
                seedExecutor.submit(() -> {
                    try {
                        PublicationResponseDTO publication =
                                publicationService.createPublication(book);

                        NoticeRequestDTO notice = new NoticeRequestDTO();
                        notice.setPosterId(1L);
                        notice.setPublicationId(publication.getId());

                        noticeService.createNotice(notice);

                        int current = count.incrementAndGet();

                        if (current % 100 == 0) {
                            log.info("Seeded {} / {}", current, books.size());
                        }

                    } catch (Exception e) {
                        log.error("Failed seeding book: {}", book.getTitle());
                    }

                })
        );

        seedExecutor.shutdown();
        seedExecutor.awaitTermination(2, TimeUnit.HOURS);

        log.info("Seeding finished: {}", count.get());

        return "Seeded " + count.get() + " books";
    }
}