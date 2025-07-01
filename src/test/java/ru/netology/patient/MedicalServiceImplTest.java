package ru.netology.patient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTest {
    PatientInfo patientTest = new PatientInfo("1", "Ivan", "Petrov", LocalDate.of(1985, 6,
            30), new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));

    @Test
    public void checkBloodPressureTest() {
        // arrange
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString())).thenReturn(patientTest);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoRepository, alertService);

        //act
        sut.checkBloodPressure("1", new BloodPressure(140, 100)); // сообщение отправлено
        sut.checkBloodPressure("1", new BloodPressure(120, 80)); // нет сообщения
        sut.checkBloodPressure("1", new BloodPressure(100, 60)); // сообщение отправлено

        //assert
        Mockito.verify(patientInfoRepository, Mockito.times(3)).getById("1");
        Mockito.verify(alertService, Mockito.times(2)).send(Mockito.anyString());
    }

    @Test
    public void checkBloodPressure_sendMessageTest() {
        // arrange
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientTest);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoRepository, alertService);

        //act
        sut.checkBloodPressure("1", new BloodPressure(140, 100));

        //assert
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
    }

    @Test
    public void checkTemperatureTest() {
        // arrange
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientTest);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoRepository, alertService);

        //act
        sut.checkTemperature("1", new BigDecimal("35.0")); // сообщение отправлено; необычная логика метода, реагирует на понижение температуры
        sut.checkTemperature("1", new BigDecimal("36.6")); // нет сообщения
        sut.checkTemperature("1", new BigDecimal("38.0")); // нет сообщения

        //assert
        Mockito.verify(patientInfoRepository, Mockito.times(3)).getById("1");
        Mockito.verify(alertService, Mockito.times(1)).send(Mockito.anyString());
    }

    @Test
    public void checkTemperature_sendMessageTest() {
        // arrange
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientTest);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoRepository, alertService);

        //act
        sut.checkTemperature("1", new BigDecimal("35.0"));

        //assert
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());
    }

}