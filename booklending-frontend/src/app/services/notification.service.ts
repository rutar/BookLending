import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NotificationDialogComponent } from '../notification-dialog/notification-dialog.component'; // Adjust the path if necessary

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private dialog: MatDialog) {}

  openDialog(message: string, isError: boolean) {
    this.dialog.open(NotificationDialogComponent, {
      data: { message, isError },
      width: '300px'
    });
  }
}
