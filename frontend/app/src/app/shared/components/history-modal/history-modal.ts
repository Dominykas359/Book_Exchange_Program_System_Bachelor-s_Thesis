import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryResponseDto } from '../../../core/models/history.model';

@Component({
  selector: 'app-history-modal',
  standalone: true,
  imports: [],
  templateUrl: './history-modal.html',
  styleUrl: './history-modal.scss'
})
export class HistoryModalComponent {
  @Input({ required: true }) history!: HistoryResponseDto;
  @Output() close = new EventEmitter<void>();

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.close.emit();
    }
  }
}
