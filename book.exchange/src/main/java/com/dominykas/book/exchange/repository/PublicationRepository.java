package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
