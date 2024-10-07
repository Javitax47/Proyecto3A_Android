# Proyecto3A_Android

Este proyecto es una aplicación Android que utiliza beacons para medir niveles de ozono y temperatura. La aplicación se conecta a un servidor para almacenar y recuperar datos de sensores a través de una API REST.

## Funcionalidades

- **Detección de Dispositivos BTLE**: La aplicación busca y se conecta a beacons Bluetooth Low Energy.
- **Medición de Sensores**: Recopila datos de sensores (ozono y temperatura) y los envía al servidor.
- **Gestión de Usuarios**: Permite la creación y eliminación de usuarios en el sistema.
- **Interfaz de Usuario**: Interfaz sencilla para interactuar con la aplicación y visualizar datos.

## Requisitos

- Android Studio
- SDK de Android (versión mínima recomendada: 21)
- Dependencias:
  - Retrofit para la comunicación con la API REST
  - Gson para la conversión de JSON

## Estructura del Proyecto

```
/app 
    /src 
        /main 
            /java 
                /com.example.usuario_upv.proyecto3a
                - MainActivity.java 
                - RetrofitClient.java 
                - SensorApi.java 
                - SensorData.java 
                - User.java 
                - Utilidades.java 
                - TramaIBeacon.java 
            /res 
                /layout 
                /drawable 
                /values
```

### Estructura de la Base de Datos

- **Tabla `sensors`**:
  - `id`: Identificador único.
  - `type`: Tipo de sensor (ozono, temperatura).
  - `value`: Valor medido.
  - `timestamp`: Marca de tiempo de la medición.
  - `user_id`: Identificador del usuario.

- **Tabla `users`**:
  - `id`: Identificador único.
  - `username`: Nombre de usuario.

## Instalación

1. Clona este repositorio:
    
    ```
    git clone https://github.com/Javitax47/Proyecto3A_Android.git
    ```

2. Abre el proyecto en Android Studio.

3. Asegúrate de tener configuradas las dependencias necesarias en tu archivo build.gradle.

4. Ejecuta la aplicación en un dispositivo Android.

## Uso

1. Abre la aplicación en tu dispositivo Android.

2. Usa los botones de la interfaz para buscar dispositivos BTLE y detener la búsqueda.

3. Se mostrarán los datos de los sensores en la interfaz.

## Contribuciones

Si deseas contribuir a este proyecto, por favor, crea un fork del repositorio y envía un pull request con tus cambios.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.