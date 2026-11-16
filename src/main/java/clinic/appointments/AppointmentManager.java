package clinic.appointments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import clinic.common.Types.Appointment;
import clinic.common.Types.AppointmentStatus;

public class AppointmentManager {

    private final Map<String, Appointment> appointmentMap;
    private final Map<String, Set<String>> doctorSchedule;
    
    private int idCounter;

    // Initializes appointmentMap and doctorSchedule as HashMaps and idCounter to 1
    public AppointmentManager() {
        this.appointmentMap = new HashMap<>();
        this.doctorSchedule = new HashMap<>();
        this.idCounter = 1;
    }

    // Checks if a specific doctor is available at specific time
    // Calls containsKey(), get(), contains()
    // Called by scheduleAppointment()
    public boolean isDoctorFree(String doctorId, String startTime) {
        if (!doctorSchedule.containsKey(doctorId)) {
            return true;
        }
        return !doctorSchedule.get(doctorId).contains(startTime);
    }

    // Checks if doctor is free, creates a unique ID for the appointment and returns appointment object
    // Calls isDoctorFree(), IllegalStateException(), format(), Appointment(), put(), computeIfAbsent(), TreeSet<>(), add()
    // Called by CLI menu
    public Appointment scheduleAppointment(String patientId, String doctorId, String startTime) {
        // Checks doctor availability
        if (!isDoctorFree(doctorId, startTime)) {
            throw new IllegalStateException("Doctor " + doctorId + " is already booked at " + startTime);
        }

        // Create ID for appointment
        String apptId = String.format("APT-%03d", idCounter++);

        // Build appointment object
        Appointment newAppt = new Appointment();
        newAppt.id = apptId;
        newAppt.patientId = patientId;
        newAppt.doctorId = doctorId;
        newAppt.startTime = startTime;
        newAppt.status = AppointmentStatus.SCHEDULED;

        // Store object in maps using appointment ID
        appointmentMap.put(apptId, newAppt);
        doctorSchedule.computeIfAbsent(doctorId, k -> new TreeSet<>()).add(startTime);

        return newAppt;
    }

    // Checks if appointment exists, cancels if scheduled and makes the doctor's slot available
    // Calls get(), containsKey(), remove()
    // Called by CLI menu
    public boolean cancelAppointment(String appointmentId) {
        // Check if ID exists
        Appointment appt = appointmentMap.get(appointmentId);
        if (appt == null) {
            return false;
        }

        // Cancel if currently scheduled
        if (appt.status != AppointmentStatus.SCHEDULED) {
            return false;
        }

        // Mark as CANCELLED and free the doctor's slot
        appt.status = AppointmentStatus.CANCELLED;
        if (doctorSchedule.containsKey(appt.doctorId)) {
            doctorSchedule.get(appt.doctorId).remove(appt.startTime);
        }

        return true;
    }

    // Gets today's date and returns all appointments for today
    // Calls now(), toString(), getAppointmentsForDate()
    // Called by CLI menu
    public List<Appointment> getTodaysAppointments() {
        String today = LocalDate.now().toString();
        return getAppointmentsForDate(today);
    }

    // Sorts appointment for date and returns them excluding cancelled ones
    // Calls ArrayList<>(), startsWith(), values(), add(), comparing()
    // Called by getTodaysAppointments()
    public List<Appointment> getAppointmentsForDate(String datePrefix) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment appt: appointmentMap.values()) {
            if (appt.startTime != null && appt.startTime.startsWith(datePrefix) && appt.status != AppointmentStatus.CANCELLED) {
                result.add(appt);
            }
        }

        result.sort(Comparator.comparing(appt -> appt.startTime));
        return result;
    }

}