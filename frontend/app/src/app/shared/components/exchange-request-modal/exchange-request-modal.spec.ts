import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExchangeRequestModal } from './exchange-request-modal';

describe('ExchangeRequestModal', () => {
  let component: ExchangeRequestModal;
  let fixture: ComponentFixture<ExchangeRequestModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExchangeRequestModal],
    }).compileComponents();

    fixture = TestBed.createComponent(ExchangeRequestModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
