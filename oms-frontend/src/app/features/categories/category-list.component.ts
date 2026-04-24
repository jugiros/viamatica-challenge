import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryService } from './category.service';
import { CategoryModel } from '../../core/models';

@Component({
  selector: 'app-category-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.scss'
})
export class CategoryListComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  
  categories = signal<CategoryModel[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  ngOnInit() {
    this.loadCategories();
  }
  
  private loadCategories() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories || []);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Error al cargar categorías. Por favor, intenta nuevamente.');
        this.isLoading.set(false);
      }
    });
  }

  deleteCategory(id: number) {
    if (confirm('¿Estás seguro de eliminar esta categoría?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.loadCategories();
        },
        error: (error) => {
          this.errorMessage.set('Error al eliminar la categoría.');
        }
      });
    }
  }
}
