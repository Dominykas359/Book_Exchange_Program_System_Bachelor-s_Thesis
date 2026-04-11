import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExchangeRequestsPage } from './exchange-requests-page';

describe('ExchangeRequestsPage', () => {
  let component: ExchangeRequestsPage;
  let fixture: ComponentFixture<ExchangeRequestsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExchangeRequestsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ExchangeRequestsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
