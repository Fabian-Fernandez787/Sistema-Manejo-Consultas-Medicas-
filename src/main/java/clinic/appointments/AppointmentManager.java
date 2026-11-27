package clinic.appointments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    private final Map<String, Set<String>> patientSchedule;
    private final Map<String, List<Visit>> patientHistory;
    private final Deque<Visit> recentVisits;

    private int idCounter;

    // Initializes appointmentMap, doctorSchedule, patientSchedule, patientHistory, recentVisits, and idCounter
    // Calls: HashMap, ArrayDeque
    // Called by: CLI modes when creating an AppointmentManager instance
    public AppointmentManager() {
        this.appointmentMap = new HashMap<>();
        this.doctorSchedule = new HashMap<>();
        this.patientSchedule = new HashMap<>();
        this.patientHistory = new HashMap<>();
        this.recentVisits = new ArrayDeque<>();
        this.idCounter = 1;
    }

    // Validates and normalizes an ISO-8601 appointment timestamp string
    // Calls: isBlank(), trim(), LocalDateTime.parse(), IllegalArgumentException()
    // Called by: isDoctorFree(), isPatientFree(), scheduleAppointment()
    private static String normalizeStartTime(String startTime) {
        if (startTime == null || startTime.isBlank()) {
            throw new IllegalArgumentException("Start time cannot be null or blank");
        }
        String trimmed = startTime.trim();
        try {
            LocalDateTime.parse(trimmed);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid start time format (expected ISO-8601, e.g., AAAA-MM-DDTHH:MM): " + trimmed);
        }
        return trimmed;
    }

    // Checks if a specific doctor has no booked appointment at startTime
    // Calls: isBlank(), normalizeStartTime(), doctorSchedule.get(), contains()
    // Called by: scheduleAppointment()
    @Override
    public boolean isDoctorFree(String doctorId, String startTime) {
        if (doctorId == null || doctorId.isBlank()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or blank");
        }
        String normalizedTime = normalizeStartTime(startTime);
        Set<String> bookedSlots = doctorSchedule.get(doctorId);
        return bookedSlots == null || !bookedSlots.contains(normalizedTime);
    }

    // Checks if a specific patient has no booked appointment at startTime
    // Calls: isBlank(), normalizeStartTime(), patientSchedule.get(), contains()
    // Called by: scheduleAppointment()
    public boolean isPatientFree(String patientId, String startTime) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient ID cannot be null or blank");
        }
        String normalizedTime = normalizeStartTime(startTime);
        Set<String> bookedSlots = patientSchedule.get(patientId);
        return bookedSlots == null || !bookedSlots.contains(normalizedTime);
    }

    // Validates inputs and availability, creates an appointment, and records slots for doctor and patient
    // Calls: isBlank(), normalizeStartTime(), isDoctorFree(), isPatientFree(), IllegalStateException(), format(), Appointment(), appointmentMap.put(), computeIfAbsent(), TreeSet<>, add()
    // Called by: CLI menu
    @Override
    public Appointment scheduleAppointment(String patientId, String doctorId, String startTime) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient ID cannot be null or blank");
        }
        if (doctorId == null || doctorId.isBlank()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or blank");
        }

        String normalizedTime = normalizeStartTime(startTime);

        // Ensure neither the doctor nor the patient already has a slot at this time
        if (!isDoctorFree(doctorId, normalizedTime)) {
            throw new IllegalStateException("Doctor " + doctorId + " is already booked at " + normalizedTime);
        }
        if (!isPatientFree(patientId, normalizedTime)) {
            throw new IllegalStateException("Patient " + patientId + " already has an appointment at " + normalizedTime);
        }

        String apptId = String.format("APT-%03d", idCounter++);

        Appointment newAppt = new Appointment();
        newAppt.id = apptId;
        newAppt.patientId = patientId;
        newAppt.doctorId = doctorId;
        newAppt.startTime = normalizedTime;
        newAppt.status = AppointmentStatus.SCHEDULED;

        appointmentMap.put(apptId, newAppt);
        doctorSchedule.computeIfAbsent(doctorId, k -> new TreeSet<>()).add(normalizedTime);
        patientSchedule.computeIfAbsent(patientId, k -> new TreeSet<>()).add(normalizedTime);

        return newAppt;
    }

    // Releases a booked time slot from both doctorSchedule and patientSchedule
    // Calls: containsKey(), get(), remove()
    // Called by: cancelAppointment(), addVisit()
    private void releaseSlot(Appointment appt) {
        if (appt.startTime == null) {
            return;
        }
        if (appt.doctorId != null && doctorSchedule.containsKey(appt.doctorId)) {
            doctorSchedule.get(appt.doctorId).remove(appt.startTime);
        }
        if (appt.patientId != null && patientSchedule.containsKey(appt.patientId)) {
            patientSchedule.get(appt.patientId).remove(appt.startTime);
        }
    }

    // Marks a scheduled appointment as CANCELLED and frees the doctor and patient time slots
    // Calls: isBlank(), appointmentMap.get(), releaseSlot()
    // Called by: CLI menu
    @Override
    public boolean cancelAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) {
            return false;
        }

        Appointment appt = appointmentMap.get(appointmentId);
        if (appt == null) {
            return false;
        }

        if (appt.status != AppointmentStatus.SCHEDULED) {
            return false;
        }

        appt.status = AppointmentStatus.CANCELLED;
        releaseSlot(appt);

        return true;
    }

    // Gets today's date and returns non-cancelled appointments scheduled for today
    // Calls: LocalDate.now(), toString(), getAppointmentsForDate()
    // Called by: CLI menu
    @Override
    public List<Appointment> getTodaysAppointments() {
        String today = LocalDate.now().toString();
        return getAppointmentsForDate(today);
    }

    // Filters non-cancelled appointments matching datePrefix and sorts them by startTime
    // Calls: ArrayList<>, isBlank(), appointmentMap.values(), startsWith(), add(), sort(), Comparator.comparing(), nullsLast(), naturalOrder()
    // Called by: getTodaysAppointments()
    public List<Appointment> getAppointmentsForDate(String datePrefix) {
        List<Appointment> result = new ArrayList<>();
        if (datePrefix == null || datePrefix.isBlank()) {
            return result;
        }

        for (Appointment appt : appointmentMap.values()) {
            if (appt != null
                    && appt.startTime != null
                    && appt.startTime.startsWith(datePrefix)
                    && appt.status != AppointmentStatus.CANCELLED) {
                result.add(appt);
            }
        }

        // Keep null timestamps at the end if any exist
        result.sort(Comparator.comparing(
                (Appointment appt) -> appt.startTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
        return result;
    }

    // Records a completed visit in patient history and recentVisits, completing any matching scheduled appointment
    // Calls: isBlank(), IllegalArgumentException(), computeIfAbsent(), ArrayList<>, add(), recentVisits.addFirst(), appointmentMap.values(), equals(), releaseSlot()
    // Called by: WaitingRoomServiceImpl.finishVisit()
    @Override
    public void addVisit(Visit visit) {
        if (visit == null || visit.patientId == null || visit.patientId.isBlank()) {
            throw new IllegalArgumentException("Visit and patientId cannot be null or blank");
        }

        patientHistory.computeIfAbsent(visit.patientId, k -> new ArrayList<>()).add(visit);
        recentVisits.addFirst(visit);

        for (Appointment appt : appointmentMap.values()) {
            if (appt.status == AppointmentStatus.SCHEDULED
                    && visit.patientId.equals(appt.patientId)
                    && visit.doctorId != null
                    && visit.doctorId.equals(appt.doctorId)) {
                appt.status = AppointmentStatus.COMPLETED;
                releaseSlot(appt);
                break;
            }
        }
    }

    // Returns a defensive copy of a patient's chronological visit history
    // Calls: isBlank(), patientHistory.containsKey(), ArrayList<>, patientHistory.get()
    // Called by: CLI menu
    @Override
    public List<Visit> getHistory(String patientId) {
        if (patientId == null || patientId.isBlank() || !patientHistory.containsKey(patientId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(patientHistory.get(patientId));
    }

    // Returns up to n most recently completed visits, ordered newest first
    // Calls: ArrayList<>, add()
    // Called by: CLI menu
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