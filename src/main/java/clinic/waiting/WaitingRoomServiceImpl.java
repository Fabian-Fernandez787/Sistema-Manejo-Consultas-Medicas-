package clinic.waiting;

import clinic.common.Types;
import clinic.common.Interfaces;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;

public class WaitingRoomServiceImpl implements Interfaces.IWaitingRoomModule {

    private Interfaces.IRegistryModule registry;
    private Interfaces.IAppointmentsModule appointments;

    private Queue<String> normalQueue = new LinkedList<>();
    private HashSet<String> waitingIds = new HashSet<>();

    public WaitingRoomServiceImpl(Interfaces.IRegistryModule registry, Interfaces.IAppointmentsModule appointments) {

        this.registry = registry;
        this.appointments = appointments;
    }

    
    @Override
    public boolean checkIn(String patientId, int emergencyLevel) {
        return false;
    }

    
    @Override
    public List<String> listWaiting() {
        return new ArrayList<>();
    }

    
    @Override
    public String callNext(String doctorId) {
        return null;
    }

    
    @Override
    public void recordVitals(String patientId, String nurseId, String vitals) {
    }

    
    @Override
    public Types.Visit finishVisit(String patientId, String doctorId, String nurseId, String notes) {
        return null;
    }
}
