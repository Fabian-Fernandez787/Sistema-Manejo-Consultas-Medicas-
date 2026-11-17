package clinic.appointments;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import clinic.common.Interfaces.IAppointmentsModule;
import clinic.common.Types.Appointment;
import clinic.common.Types.AppointmentStatus;
import clinic.common.Types.Visit;

public class AppointmentManager implements IAppointmentsModule {

    private final Map<String, Appointment> appointmentMap;
    private final Map<String, Set<String>> doctorSchedule;
    private final Map<String, List<Visit>> patientHistory;
    private final Deque<Visit> recentVisits;
    
    private int idCounter;

    // Initializes appointmentMap and doctorSchedule as HashMaps and idCounter to 1
    public AppointmentManager() {
        this.appointmentMap = new HashMap<>();
        this.doctorSchedule = new HashMap<>();
        this.patientHistory = new HashMap<>();
        this.recentVisits = new ArrayDeque<>();
        this.idCounter = 1;
    }

    // Checks if a specific doctor is available at specific time
    // Calls containsKey(), get(), contains()
    // Called by scheduleAppointment()
    @Override 
    public boolean isDoctorFree(String doctorId, String startTime) {
        if (!doctorSchedule.containsKey(doctorId)) {
            return true;
        }
        return !doctorSchedule.get(doctorId).contains(startTime);
    }

    // Checks if doctor is free, creates a unique ID for the appointment and returns appointment object
    // Calls isDoctorFree(), IllegalStateException(), format(), Appointment(), put(), computeIfAbsent(), TreeSet<>(), add()
    // Called by CLI menu
    @Override 
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
    @Override 
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
    @Override 
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

    // Appends visit to patient's history, pushes to recent visits deque, and marks matching appointment COMPLETED
    // Calls computeIfAbsent(), ArrayList<>(), add(), addFirst(), values(), equals(), containsKey(), remove()
    // Called by WaitingRoomModule.finishVisit() 
    @Override
    public void addVisit(Visit visit) {
        if (visit == null || visit.patientId == null) {
            throw new IllegalArgumentException("Visit and patientId cannot be null");
        }

        // Append to medical history (oldest to newest)
        patientHistory.computeIfAbsent(visit.patientId, k -> new ArrayList<>()).add(visit);

        // Push to the front of recentVisits deque
        recentVisits.addFirst(visit);

        // Find matching SCHEDULED appointment for this patient & doctor and mark COMPLETED
        for (Appointment appt : appointmentMap.values()) {
            if (appt.status == AppointmentStatus.SCHEDULED && appt.patientId.equals(visit.patientId)&& appt.doctorId.equals(visit.doctorId)) {
                appt.status = AppointmentStatus.COMPLETED;
                if (doctorSchedule.containsKey(appt.doctorId)) {
                    doctorSchedule.get(appt.doctorId).remove(appt.startTime);
                }
                break;
            }
        }
    }

    // Returns a patient's chronological visit history from oldest to newest (empty list if none)
    // Calls containsKey(), ArrayList<>(), get()
    // Called by CLI menu
    @Override
    public List<Visit> getHistory(String patientId) {
        if (patientId == null || !patientHistory.containsKey(patientId)) {
            return new ArrayList<>();
        }
        // Return a defensive copy so external callers cannot accidentally modify internal history
        return new ArrayList<>(patientHistory.get(patientId));
    }

    // Returns up to n most recently completed visits, ordered newest first
    // Calls ArrayList<>(), add()
    // Called by CLI menu
    @Override
    public List<Visit> getRecentCompleted(int n) {
        List<Visit> result = new ArrayList<>();
        if (n <= 0) {
            return result;
        }

        int count = 0;
        for (Visit visit : recentVisits) {
            if (count >= n) {
                break;
            }
            result.add(visit);
            count++;
        }
        return result;
    }

}