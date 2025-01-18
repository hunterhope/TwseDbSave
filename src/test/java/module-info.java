/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module TwseDBTest {
    requires TwseDB;
    requires JsonRequest;
    requires java.sql;
    requires spring.jdbc;
    requires org.junit.jupiter.api;
    requires org.mockito;
    opens com.hunterhope.twsedbsave.service.test;
    opens com.hunterhope.twsedbsave.dao.impl.test;
}
