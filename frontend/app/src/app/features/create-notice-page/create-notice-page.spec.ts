import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateNoticePage } from './create-notice-page';

describe('CreateNoticePage', () => {
  let component: CreateNoticePage;
  let fixture: ComponentFixture<CreateNoticePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateNoticePage],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateNoticePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
