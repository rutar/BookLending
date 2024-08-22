import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeComponent], // Declare HomeComponent
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Trigger change detection to update the view
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // Test to check if the component's template is rendering correctly
  it('should render content in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    // Check if the template renders some default content
    // Update the selector based on the actual content in your home.component.html
    // For example, checking for a specific element or text
    expect(compiled.querySelector('h2')?.textContent).toContain('Welcome to the Home Page');
  });
});
