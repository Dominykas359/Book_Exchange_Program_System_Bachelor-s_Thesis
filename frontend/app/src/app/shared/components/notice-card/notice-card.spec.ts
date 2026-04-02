import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeCard } from './notice-card';

describe('NoticeCard', () => {
  let component: NoticeCard;
  let fixture: ComponentFixture<NoticeCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoticeCard],
    }).compileComponents();

    fixture = TestBed.createComponent(NoticeCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
