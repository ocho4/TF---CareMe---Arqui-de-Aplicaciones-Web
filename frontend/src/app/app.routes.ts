import { Routes } from '@angular/router';
import { rolGuard } from './guards/rol.guard';
import { AuthRegistroComponent } from './componente/auth-registro/auth-registro.component';
import { AuthLoginComponent } from './componente/auth-login/auth-login.component';
import { AuthRecuperarComponent } from './componente/auth-recuperar/auth-recuperar.component';
import { CuidadorBuscarComponent } from './componente/cuidador-buscar/cuidador-buscar.component';
import { ServicioSolicitarComponent } from './componente/servicio-solicitar/servicio-solicitar.component';
import { ServicioPendientesComponent } from './componente/servicio-pendientes/servicio-pendientes.component';
import { ServicioCancelarComponent } from './componente/servicio-cancelar/servicio-cancelar.component';
import { ChatComponent } from './componente/chat/chat.component';
import { ServicioProximosComponent } from './componente/servicio-proximos/servicio-proximos.component';
import { ServicioAgendaComponent } from './componente/servicio-agenda/servicio-agenda.component';
import { PagoComponent } from './componente/pago/pago.component';
import { CotizacionComponent } from './componente/cotizacion/cotizacion.component';
import { DashboardComponent } from './componente/dashboard/dashboard.component';
import { PerfilComponent } from './componente/perfil/perfil.component';
import { SeguimientoComponent } from './componente/seguimiento/seguimiento.component';
import { PacientesComponent } from './componente/pacientes/pacientes.component';
import { AdminComponent } from './componente/admin/admin.component';
import { PendienteAprobacionComponent } from './componente/pendiente-aprobacion/pendiente-aprobacion.component';
import { MisPagosComponent } from './componente/mis-pagos/mis-pagos.component';
import { FavoritosComponent } from './componente/favoritos/favoritos.component';

export const routes: Routes = [
  { path: '',              redirectTo: 'login', pathMatch: 'full' },
  { path: 'dashboard',    component: DashboardComponent },
  { path: 'perfil',       component: PerfilComponent },
  { path: 'registro',      component: AuthRegistroComponent },
  { path: 'login',              component: AuthLoginComponent },
  { path: 'recuperar-password', component: AuthRecuperarComponent },
  { path: 'buscar',        component: CuidadorBuscarComponent },
  { path: 'solicitar/:idCuidador', component: ServicioSolicitarComponent },
  { path: 'pendientes',    component: ServicioPendientesComponent, canActivate: [rolGuard(['cuidador'])] },
  { path: 'mis-servicios',        component: ServicioCancelarComponent },
  { path: 'chat/:idServicio',    component: ChatComponent },
  { path: 'recordatorios',       component: ServicioProximosComponent },
  { path: 'agenda',              component: ServicioAgendaComponent },
  { path: 'seguimiento/:idServicio', component: SeguimientoComponent },
  { path: 'pago/:idServicio',    component: PagoComponent },
  { path: 'cotizacion',          component: CotizacionComponent },
  { path: 'pacientes',              component: PacientesComponent },
  { path: 'admin',                  component: AdminComponent },
  { path: 'pendiente-aprobacion',   component: PendienteAprobacionComponent },
  { path: 'mis-pagos',              component: MisPagosComponent },
  { path: 'favoritos',              component: FavoritosComponent },
];
