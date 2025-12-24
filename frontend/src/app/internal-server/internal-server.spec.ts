import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InternalServer } from './internal-server';

describe('InternalServer', () => {
  let component: InternalServer;
  let fixture: ComponentFixture<InternalServer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InternalServer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InternalServer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
