# ProyectoCita

## Descripción
ProyectoCita es una aplicación móvil desarrollada en Kotlin utilizando Android Studio. Su propósito es gestionar citas médicas, permitiendo a los usuarios iniciar sesión, programar citas, cancelar citas y ver el historial de citas registradas en la base de datos.

## Características
- **Inicio de sesión:** Permite el acceso de usuarios mediante credenciales la cual su usuario es su nombre y clave su numero de cedula. Se incluye una cuenta de administrador con usuario "Administrador" y contraseña "1234".
- **Gestión de citas:** Los usuarios pueden programar, ver y cancelar citas médicas.
- **Base de datos SQLite:** Se utiliza Room para almacenar la información de usuarios y citas.
- **Interfaz amigable:** Implementa `RecyclerView` y `ScrollView` para mostrar información de manera clara.
- **Accesibilidad:** Se puede acceder a diferentes especialidades médicas desde la aplicación.

## Estructura del Proyecto

### Paquete principal: `com.example.proyectocita`

- **`MainActivity.kt`**: Pantalla de inicio de sesión.
- **`MenuActivity.kt`**: Menú principal con opciones para gestionar citas.
- **`PoliceActivity.kt`**: Permite elegir especialidad médica.
- **`SaludActivity.kt`**: Muestra citas agendadas y permite cancelarlas.
- **`TransitoActivity.kt`**: Muestra un historial de citas.

### Base de Datos
El proyecto utiliza `CitaDatabase` con Room para gestionar la información.
- **`UsuarioDao.kt`**: Maneja los datos de usuarios.
- **`CitaDao.kt`**: Permite operaciones CRUD sobre las citas.
- **`CitaDatabase.kt`**: Instancia y configura la base de datos SQLite.

## Instalación y Configuración
1. Clonar el repositorio:
   ```sh
   git clone https://github.com/wilsonsanz/proyectocita
   ```
2. Abrir en Android Studio.
3. Ejecutar en un dispositivo o emulador.

## Requisitos
- Android Studio
- Kotlin
- API mínima recomendada: 24 (Android 7.0 Nougat)

## Autor
Wilson Sánchez

## Licencia
Este proyecto está bajo la licencia MIT. Puedes modificarlo y distribuirlo libremente.

