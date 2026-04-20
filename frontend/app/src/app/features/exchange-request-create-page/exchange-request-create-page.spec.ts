import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExchangeRequestCreatePage } from './exchange-request-create-page';

describe('ExchangeRequestCreatePage', () => {
  let component: ExchangeRequestCreatePage;
  let fixture: ComponentFixture<ExchangeRequestCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExchangeRequestCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ExchangeRequestCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
