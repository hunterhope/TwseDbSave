/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module TwseDbSaveTest {
    requires TwseDbSave;
    requires JsonRequest;
    requires org.junit.jupiter.api;
    requires org.mockito;
    opens com.hunterhope.twsedbsave.service.test;
}
