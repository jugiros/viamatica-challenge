import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { OrderFormComponent } from './order-form.component';
import { OrderService } from './order.service';
import { ProductService } from '../products/product.service';
import { ProductModel, CreateOrderRequest } from '../../core/models';
import { ToastService } from '../../shared/services/toast.service';
import { CurrencyLocalePipe } from '../../shared/pipes/currency-locale.pipe';

describe('OrderFormComponent', () => {
  let component: OrderFormComponent;
  let fixture: ComponentFixture<OrderFormComponent>;
  let mockOrderService: jasmine.SpyObj<OrderService>;
  let mockProductService: jasmine.SpyObj<ProductService>;
  let mockToastService: jasmine.SpyObj<ToastService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockProducts: ProductModel[] = [
    { id: 1, name: 'Product 1', description: 'Description 1', price: 100, stock: 10, categoryId: 1, active: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
    { id: 2, name: 'Product 2', description: 'Description 2', price: 200, stock: 20, categoryId: 2, active: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
  ];

  const mockOrder = { id: 1, status: 'PENDIENTE', total: 300, createdAt: '2024-01-01T00:00:00Z' };

  beforeEach(async () => {
    mockOrderService = jasmine.createSpyObj('OrderService', ['createOrder']);
    mockProductService = jasmine.createSpyObj('ProductService', ['getProducts']);
    mockToastService = jasmine.createSpyObj('ToastService', ['showSuccess', 'showError']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    mockProductService.getProducts.and.returnValue(of(mockProducts));
    mockOrderService.createOrder.and.returnValue(of(mockOrder));

    await TestBed.configureTestingModule({
      imports: [OrderFormComponent, CurrencyLocalePipe],
      providers: [
        { provide: OrderService, useValue: mockOrderService },
        { provide: ProductService, useValue: mockProductService },
        { provide: ToastService, useValue: mockToastService },
        { provide: Router, useValue: mockRouter },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(OrderFormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Signals', () => {
    it('availableProducts debe ser array vacío inicialmente', () => {
      expect(component.availableProducts()).toEqual([]);
    });

    it('orderItems debe ser array vacío inicialmente', () => {
      expect(component.orderItems()).toEqual([]);
    });

    it('isLoading debe ser false inicialmente', () => {
      expect(component.isLoading()).toBe(false);
    });

    it('errorMessage debe ser null inicialmente', () => {
      expect(component.errorMessage()).toBeNull();
    });

    it('successMessage debe ser null inicialmente', () => {
      expect(component.successMessage()).toBeNull();
    });
  });

  describe('Computed total', () => {
    it('total debe ser 0 cuando no hay items', () => {
      expect(component.total()).toBe(0);
    });

    it('debe calcular total en tiempo real al añadir productos', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 2 }]);
      
      // Forzar ciclo de detección de cambios (OnPush)
      fixture.detectChanges();

      expect(component.total()).toBe(200); // 100 * 2
    });

    it('debe calcular total con múltiples productos', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([
        { productId: 1, quantity: 1 },
        { productId: 2, quantity: 2 }
      ]);
      
      fixture.detectChanges();

      expect(component.total()).toBe(500); // 100 + 200 * 2
    });

    it('debe actualizar total al cambiar cantidad', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      fixture.detectChanges();
      expect(component.total()).toBe(100);

      component.orderItems.set([{ productId: 1, quantity: 3 }]);
      fixture.detectChanges();
      expect(component.total()).toBe(300);
    });
  });

  describe('addToOrder', () => {
    it('debe añadir producto a la orden si no existe', () => {
      component.availableProducts.set(mockProducts);
      const product = mockProducts[0];
      
      component.addToOrder(product);
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([{ productId: 1, quantity: 1 }]);
      expect(component.isProductInOrder(1)).toBe(true);
    });

    it('debe incrementar cantidad si producto ya existe', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      const product = mockProducts[0];
      
      component.addToOrder(product);
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([{ productId: 1, quantity: 2 }]);
    });

    it('no debe incrementar si cantidad alcanza stock máximo', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 10 }]);
      const product = mockProducts[0];
      
      component.addToOrder(product);
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([{ productId: 1, quantity: 10 }]);
    });
  });

  describe('removeFromOrder', () => {
    it('debe decrementar cantidad si cantidad > 1', () => {
      component.orderItems.set([{ productId: 1, quantity: 2 }]);
      
      component.removeFromOrder(1);
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([{ productId: 1, quantity: 1 }]);
    });

    it('debe eliminar producto si cantidad es 1', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      
      component.removeFromOrder(1);
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([]);
      expect(component.isProductInOrder(1)).toBe(false);
    });
  });

  describe('isProductInOrder', () => {
    it('debe retornar true si producto está en la orden', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      expect(component.isProductInOrder(1)).toBe(true);
    });

    it('debe retornar false si producto no está en la orden', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      expect(component.isProductInOrder(2)).toBe(false);
    });
  });

  describe('getProductQuantity', () => {
    it('debe retornar cantidad correcta del producto', () => {
      component.orderItems.set([{ productId: 1, quantity: 3 }]);
      expect(component.getProductQuantity(1)).toBe(3);
    });

    it('debe retornar 0 si producto no está en la orden', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      expect(component.getProductQuantity(2)).toBe(0);
    });
  });

  describe('canAddProduct', () => {
    it('debe retornar true si cantidad actual < stock', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 5 }]);
      const product = mockProducts[0];
      
      expect(component.canAddProduct(product)).toBe(true);
    });

    it('debe retornar false si cantidad actual >= stock', () => {
      component.availableProducts.set(mockProducts);
      component.orderItems.set([{ productId: 1, quantity: 10 }]);
      const product = mockProducts[0];
      
      expect(component.canAddProduct(product)).toBe(false);
    });
  });

  describe('submitOrder', () => {
    it('debe mostrar error si no hay productos', () => {
      component.submitOrder();
      fixture.detectChanges();

      expect(mockToastService.showError).toHaveBeenCalledWith('La orden no tiene productos');
      expect(component.errorMessage()).toBe('La orden no tiene productos');
      expect(mockOrderService.createOrder).not.toHaveBeenCalled();
    });

    it('debe llamar a orderService.createOrder con items correctos', () => {
      component.orderItems.set([{ productId: 1, quantity: 2 }]);
      component.submitOrder();
      fixture.detectChanges();

      const expectedRequest: CreateOrderRequest = {
        items: [{ productId: 1, quantity: 2 }]
      };
      expect(mockOrderService.createOrder).toHaveBeenCalledWith(expectedRequest);
    });

    it('debe establecer isLoading a true al enviar', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      component.submitOrder();
      fixture.detectChanges();

      expect(component.isLoading()).toBe(true);
    });

    it('debe limpiar orderItems y navegar a /orders al éxito', () => {
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      component.submitOrder();
      fixture.detectChanges();

      expect(component.orderItems()).toEqual([]);
      expect(mockToastService.showSuccess).toHaveBeenCalledWith('Orden creada exitosamente');
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/orders']);
    });

    it('debe mostrar error al fallar la creación', () => {
      mockOrderService.createOrder.and.returnValue(of(null));
      component.orderItems.set([{ productId: 1, quantity: 1 }]);
      component.submitOrder();
      fixture.detectChanges();

      expect(mockToastService.showError).toHaveBeenCalledWith('Error al crear orden');
      expect(component.errorMessage()).toBe('Error al crear orden');
    });
  });

  describe('loadProducts', () => {
    it('debe cargar productos al inicializar', () => {
      expect(mockProductService.getProducts).toHaveBeenCalled();
    });

    it('debe filtrar productos activos con stock > 0', () => {
      fixture.detectChanges();
      expect(component.availableProducts()).toEqual(mockProducts.filter(p => p.active && p.stock > 0));
    });
  });
});
