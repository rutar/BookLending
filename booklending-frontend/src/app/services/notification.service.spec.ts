import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { NotificationService } from './notification.service';
import { NotificationDialogComponent } from '../notification-dialog/notification-dialog.component';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';


describe('NotificationService', () => {
  let service: NotificationService;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(() => {
    const matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule], // Disable animations for the test
      providers: [
        NotificationService,
        {provide: MatDialog, useValue: matDialogSpy}
      ]
    });

    service = TestBed.inject(NotificationService);
    dialogSpy = TestBed.inject(MatDialog) as jasmine.SpyObj<MatDialog>;
  });

  it('should open the dialog with correct parameters', () => {
    const message = 'Test message';
    const isError = true;

    // Simulate dialog open returning an observable
    dialogSpy.open.and.returnValue({
      afterClosed: () => of(true),
    } as any);

    service.openDialog(message, isError);

    // Verify that MatDialog's open method was called with correct arguments
    expect(dialogSpy.open).toHaveBeenCalledWith(NotificationDialogComponent, {
      data: {message, isError},
      width: '300px'
    });
  });


  it('should open the dialog with isError=false', () => {
    const message = 'Another test message';
    const isError = false;

    // Simulate dialog open returning an observable
    dialogSpy.open.and.returnValue({
      afterClosed: () => of(true),
    } as any);

    service.openDialog(message, isError);

    // Verify that MatDialog's open method was called with correct arguments
    expect(dialogSpy.open).toHaveBeenCalledWith(NotificationDialogComponent, {
      data: {message, isError},
      width: '300px'
    });
  });
})

