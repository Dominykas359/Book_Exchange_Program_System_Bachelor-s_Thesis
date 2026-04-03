import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryResponseDto } from '../../../core/models/history.model';

@Component({
  selector: 'app-history-card',
  standalone: true,
  imports: [],
  templateUrl: './history-card.html',
  styleUrl: './history-card.scss'
})
export class HistoryCardComponent {
  @Input({ required: true }) history!: HistoryResponseDto;
  @Output() cardClick = new EventEmitter<HistoryResponseDto>();

  onClick(): void {
    this.cardClick.emit(this.history);
  }
}
