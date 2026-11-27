1. Título del proyecto
Sistema de Manejo para Consultas Medicas

2. Descripción
El programa funciona desde consola y tiene un menu inicial para elegir entre modo manual (tu escribes los datos) y modo automatico (ya viene con datos y ejemplos listos para probar).

Estructuras de datos que usamos y por que:
- HashMap y EnumMap: para buscar rapido por ID (pacientes, empleados, medicos, citas, historiales) y agrupar medicos por especialidad sin tener que recorrer todo cada vez.
- PriorityQueue y Queue (LinkedList): para atender primero las emergencias segun su nivel (1 a 3) y orden de llegada, y luego a los pacientes normales (nivel 0) en orden de llegada (FIFO).
- ArrayDeque: para ir poniendo las visitas terminadas al frente y sacar rapido las ultimas visitas completadas.
- TreeSet, HashSet y LinkedHashSet: para llevar los horarios ocupados de medicos y pacientes, evitar duplicados y mostrar la sala de espera en orden sin repetir IDs.
- ArrayList: para guardar el historial de visitas de cada paciente en orden y devolver listas de resultados.

Tambien valida cosas basicas como formato de fecha/hora, no reservar dos citas a la vez para el mismo medico o paciente, liberar el horario si se cancela o completa una cita, y asociar el paciente al medico cuando termina la visita.

3. Instalación
Necesitas tener Java (JDK 17 o mas reciente) instalado y una terminal o IDE (VS Code, IntelliJ, Eclipse).

1. Clona o descarga el proyecto y entra a su carpeta:
git clone <URL-DEL-REPOSITORIO>
cd <CARPETA-DEL-PROYECTO>

2. Revisa que las carpetas de paquetes esten asi dentro de src:
src/clinic/appointments/AppointmentManager.java
src/clinic/cli/Main.java
src/clinic/cli/ManualMode.java
src/clinic/cli/AutomaticMode.java
src/clinic/common/Interfaces.java
src/clinic/common/Types.java
src/clinic/registry/RegistryServiceImpl.java
src/clinic/waiting/WaitingRoomServiceImpl.java

3. Compila todo en una carpeta out:

En Mac, Linux o Git Bash:
javac -d out $(find src -name "*.java")

En Windows (PowerShell):
javac -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object FullName)

4. Uso 
Corre la clase Main desde la carpeta del proyecto:

java -cp out clinic.cli.Main

Al abrir te pregunta que modo quieres usar:
- Opcion 1 (Modo manual): te pide cada dato paso a paso (IDs, nombres, especialidades, nivel de emergencia 0 para normal o 1-3 para emergencia, fecha y hora, signos vitales, etc.).
- Opcion 2 (Modo automatico): ya carga datos semilla y ejemplos (pacientes extra, citas de hoy, una visita ya hecha y gente en espera) para probar las 18 opciones sin teclear tanto.

Ejemplo rapido:
=== SELECCIONAR MODO DE EJECUCION ===
1. Modo manual (ingresar datos paso a paso)
2. Modo automatico (datos y acciones de prueba)
0. Salir
Seleccione un modo: 2

Modo automatico iniciado.

=== SISTEMA MEDICO - MODO AUTOMATICO ===
...
15. Mostrar sala de espera
0. Regresar al menu principal
Seleccione una opcion: 15
Sala de espera:
[P103, P102, P104, P101]

Con 0 sales del modo actual y vuelves a elegir modo, o sales del programa si estas en el menu principal.

5. Configuración
No necesita variables de entorno ni archivos externos ni librerias extra; todo corre en memoria.

- Datos iniciales: al arrancar cada modo se llama loadSeedData(), que carga medicos (D101 a D104 en GENERAL, PEDIATRIC, NEURO y RETINA), enfermeras (N101, N102), secretaria (S101) y pacientes (P101 a P105).
- Formato de fecha y hora: las citas usan AAAA-MM-DDTHH:MM (por ejemplo 2026-11-27T14:30), y las citas de hoy se filtran con la fecha del dia actual.

6. Autores
-Fabian Fernandez Gonzalez
-Caleb G. Martinez Perez
-Irvin O. Pagan Bosque