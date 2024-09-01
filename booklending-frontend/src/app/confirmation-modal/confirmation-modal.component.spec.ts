import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmationModalComponent } from './confirmation-modal.component';
import { By } from '@angular/platform-browser';

describe('ConfirmationModalComponent', () => {
  let component: ConfirmationModalComponent;
  let fixture: ComponentFixture<ConfirmationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationModalComponent] // Import standalone component
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationModalComponent);
    component = fixture.componentInstance;
    component.bookTitle = 'Test Book'; // Set the input property
    fixture.detectChanges(); // Trigger change detection to render the template
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct book title', () => {
    fixture.detectChanges(); // Ensure the view is updated
    const titleElement = fixture.debugElement.query(By.css('h3'));
    expect(titleElement).toBeTruthy(); // Check that the title element exists
    expect(titleElement.nativeElement.textContent).toContain('Test Book');
  });

  it('should emit confirm event when confirm button is clicked', () => {
    spyOn(component.confirm, 'emit');

    const confirmButton = fixture.debugElement.query(By.css('.confirm-btn'));
    expect(confirmButton).toBeTruthy(); // Ensure the button exists

    confirmButton.nativeElement.click();

    expect(component.confirm.emit).toHaveBeenCalled();
  });

  it('should emit close event when close button is clicked', () => {
    spyOn(component.close, 'emit');

    const closeButton = fixture.debugElement.query(By.css('.cancel-btn'));
    expect(closeButton).toBeTruthy(); // Ensure the button exists

    closeButton.nativeElement.click();

    expect(component.close.emit).toHaveBeenCalled();
  });

  it('should call onClose when overlay is clicked', () => {
    spyOn(component, 'onClose');

    const overlay = fixture.debugElement.query(By.css('.modal-overlay'));
    expect(overlay).toBeTruthy(); // Ensure the overlay exists

    overlay.nativeElement.click();

    expect(component.onClose).toHaveBeenCalled();
  });

  it('should not call onClose when clicking inside modal content', () => {
    spyOn(component, 'onClose');

    const modalContent = fixture.debugElement.query(By.css('.modal-content'));
    expect(modalContent).toBeTruthy(); // Ensure the modal content exists

    modalContent.nativeElement.click();

    expect(component.onClose).not.toHaveBeenCalled();
  });
});
