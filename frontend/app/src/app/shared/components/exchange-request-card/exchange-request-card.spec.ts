import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExchangeRequestCard } from './exchange-request-card';

describe('ExchangeRequestCard', () => {
  let component: ExchangeRequestCard;
  let fixture: ComponentFixture<ExchangeRequestCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExchangeRequestCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ExchangeRequestCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
