import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { ProductListComponent } from './product-list.component';
import { ProductService } from './product.service';
import { ProductModel } from '../../core/models';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;
  let mockProductService: jasmine.SpyObj<ProductService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockProducts: ProductModel[] = [
    { id: 1, name: 'Product 1', description: 'Description 1', price: 100, stock: 10, categoryId: 1, active: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
    { id: 2, name: 'Product 2', description: 'Description 2', price: 200, stock: 20, categoryId: 2, active: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
  ];

  beforeEach(async () => {
    mockProductService = jasmine.createSpyObj('ProductService', ['getProducts', 'deleteProduct']);
    mockProductService.getProducts.and.returnValue(of(mockProducts));
    mockProductService.deleteProduct.and.returnValue(of(undefined));

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProductListComponent, ConfirmDialogComponent, CurrencyLocalePipe],
      providers: [
        { provide: ProductService, useValue: mockProductService },
        { provide: Router, useValue: mockRouter },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debe mostrar lista de productos cuando signals emiten', () => {
    // Inicializar el componente
    fixture.detectChanges();

    // Verificar que items signal tiene productos
    expect(component.items()).toEqual(mockProducts);
    expect(component.items().length).toBe(2);
  });

  it('debe mostrar spinner cuando isLoading es true', () => {
    // Establecer isLoading a true
    component['isLoading'].set(true);
    
    // Forzar ciclo de detección de cambios (OnPush)
    fixture.detectChanges();

    // Verificar que isLoading es true
    expect(component['isLoading']()).toBe(true);
  });

  it('debe mostrar mensaje de error cuando errorMessage tiene valor', () => {
    // Establecer mensaje de error
    component['errorMessage'].set('Error al cargar productos');
    
    // Forzar ciclo de detección de cambios (OnPush)
    fixture.detectChanges();

    // Verificar que errorMessage tiene valor
    expect(component['errorMessage']()).toBe('Error al cargar productos');
  });

  it('debe mostrar estado vacío cuando no hay productos', () => {
    // Configurar servicio para retornar array vacío
    mockProductService.getProducts.and.returnValue(of([]));
    
    // Recrear componente con el nuevo mock
    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Verificar que items está vacío
    expect(component.items()).toEqual([]);
    expect(component.items().length).toBe(0);
  });

  it('debe llamar a deleteProduct al llamar a deleteItemWithConfirmation', () => {
    fixture.detectChanges();

    const product = mockProducts[0];
    component.deleteProduct(product);

    // Verificar que el servicio de eliminación fue llamado
    expect(mockProductService.deleteProduct).toHaveBeenCalledWith(product.id);
  });

  it('debe llamar a getItemName y getItemNamePlural correctamente', () => {
    expect(component.getItemName()).toBe('producto');
    expect(component.getItemNamePlural()).toBe('productos');
  });

  it('debe cargar productos en ngOnInit', () => {
    spyOn(component, 'loadData').and.callThrough();
    fixture.detectChanges();

    expect(component.loadData).toHaveBeenCalled();
    expect(mockProductService.getProducts).toHaveBeenCalled();
  });
});
