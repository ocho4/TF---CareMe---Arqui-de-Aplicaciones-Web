import { AfterViewInit, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData } from 'chart.js';
import { PagoService, PagoHistorial } from '../../services/pago.service';
import { SesionService } from '../../services/sesion.service';

@Component({
  selector: 'app-mis-pagos',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    DecimalPipe,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    BaseChartDirective,
  ],
  templateUrl: './mis-pagos.component.html',
  styleUrl: './mis-pagos.component.css',
})
export class MisPagosComponent implements OnInit, AfterViewInit {
  private pagoService  = inject(PagoService);
  private sesionService = inject(SesionService);
  private snackBar     = inject(MatSnackBar);
  private cdr          = inject(ChangeDetectorRef);

  columnas = ['idPago', 'fecha', 'contraparte', 'metodo', 'monto', 'estado'];
  dataSource = new MatTableDataSource<PagoHistorial>();
  cargando = true;
  esFamiliar = false;
  totalGastado = 0;
  pagosCompletados = 0;

  barChartData: ChartData<'bar'> = { labels: [], datasets: [{ data: [], label: '' }] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { callback: (v) => `S/ ${v}` } } },
  };
  hayDatosGrafico = false;

  pieChartData: ChartData<'pie'> = { labels: [], datasets: [{ data: [] }] };
  pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { position: 'bottom' } },
  };

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngOnInit(): void {
    const usuario = this.sesionService.obtener();
    this.esFamiliar = usuario?.rol === 'familiar';
    if (!usuario?.idUsuario) return;

    this.pagoService.listarPorUsuario(usuario.idUsuario).subscribe({
      next: (lista) => {
        this.dataSource.data = lista;
        const completados = lista.filter(p => p.estadoPago?.toLowerCase() === 'completado' || p.estadoPago?.toLowerCase() === 'pagado');
        this.pagosCompletados = completados.length;
        this.totalGastado = completados.reduce((acc, p) => acc + (p.monto ?? 0), 0);
        this.construirGraficoMensual(completados);
        this.construirGraficoMetodo(completados);
        this.cargando = false;
        this.cdr.markForCheck();
        setTimeout(() => {
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
        this.snackBar.open('Error al cargar el historial de pagos', 'Cerrar', { duration: 3000 });
      },
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  private construirGraficoMensual(completados: PagoHistorial[]): void {
    const meses = ['Ene','Feb','Mar','Abr','May','Jun','Jul','Ago','Sep','Oct','Nov','Dic'];
    const totalesPorMes = new Map<string, number>();

    for (const pago of completados) {
      if (!pago.fechaPago) continue;
      const fecha = new Date(pago.fechaPago);
      const clave = `${fecha.getFullYear()}-${fecha.getMonth()}`;
      totalesPorMes.set(clave, (totalesPorMes.get(clave) ?? 0) + (pago.monto ?? 0));
    }

    const claves = Array.from(totalesPorMes.keys()).sort();
    this.hayDatosGrafico = claves.length > 0;

    this.barChartData = {
      labels: claves.map((clave) => {
        const [anio, mes] = clave.split('-').map(Number);
        return `${meses[mes]} ${anio}`;
      }),
      datasets: [{
        data: claves.map((clave) => totalesPorMes.get(clave) ?? 0),
        label: this.esFamiliar ? 'Gasto mensual (S/)' : 'Ingreso mensual (S/)',
        backgroundColor: '#3D44DD',
        borderRadius: 6,
      }],
    };
  }

  metodoDePago(pago: PagoHistorial): string {
    if (pago.metodoPago) {
      return pago.metodoPago.toLowerCase() === 'yape' ? 'Yape' : 'Tarjeta';
    }
    // Pagos antiguos sin metodoPago guardado: se infiere del codigo de operacion
    return pago.codigoOperacion?.startsWith('YAPE') ? 'Yape' : 'Simulado';
  }

  private construirGraficoMetodo(completados: PagoHistorial[]): void {
    const conteoPorMetodo = new Map<string, number>();

    for (const pago of completados) {
      const metodo = this.metodoDePago(pago);
      conteoPorMetodo.set(metodo, (conteoPorMetodo.get(metodo) ?? 0) + 1);
    }

    const etiquetas = Array.from(conteoPorMetodo.keys());
    this.pieChartData = {
      labels: etiquetas,
      datasets: [{
        data: etiquetas.map((etiqueta) => conteoPorMetodo.get(etiqueta) ?? 0),
        backgroundColor: ['#3D44DD', '#0891b2', '#d97706'],
      }],
    };
  }

  nombreContraparte(pago: PagoHistorial): string {
    if (this.esFamiliar) {
      const c = pago.servicio?.cuidador?.usuario;
      return `${c?.nombres ?? ''} ${c?.apellidos ?? ''}`.trim() || '—';
    } else {
      const f = pago.servicio?.familiar?.usuario;
      return `${f?.nombres ?? ''} ${f?.apellidos ?? ''}`.trim() || '—';
    }
  }

  colorEstado(estado: string): string {
    const e = estado?.toLowerCase();
    if (e === 'completado' || e === 'pagado') return 'verde';
    if (e === 'pendiente') return 'amarillo';
    return 'gris';
  }

  iconoEstado(estado: string): string {
    const e = estado?.toLowerCase();
    if (e === 'completado' || e === 'pagado') return 'check_circle';
    if (e === 'pendiente') return 'schedule';
    return 'cancel';
  }
}
