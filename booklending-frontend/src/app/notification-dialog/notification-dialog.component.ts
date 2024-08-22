import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule

@Component({
  selector: 'app-notification-dialog',
  standalone: true,
  templateUrl: './notification-dialog.component.html',
  styleUrls: ['./notification-dialog.component.scss'],
  imports: [MatDialogModule] // Add MatDialogModule to imports
})
export class NotificationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<NotificationDialogComponent>, // Inject MatDialogRef
    @Inject(MAT_DIALOG_DATA) public data: { message: string; isError: boolean }
  ) {}

  onClose(): void {
    this.dialogRef.close(); // Close the dialog
  }
}
