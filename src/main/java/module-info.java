/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module TwseDB {
    requires JsonRequest;
    requires com.google.gson;
    requires java.sql;
    requires com.google.errorprone.annotations;
    requires spring.jdbc;
    exports com.hunterhope.twsedbsave.service;
    exports com.hunterhope.twsedbsave.service.exception;
    exports com.hunterhope.twsedbsave.service.data to TwseDBTest;    
    exports com.hunterhope.twsedbsave.dao to TwseDBTest;
    exports com.hunterhope.twsedbsave.entity to TwseDBTest;
    exports com.hunterhope.twsedbsave.dao.impl to TwseDBTest;
    exports com.hunterhope.twsedbsave.other to TwseDBTest;
}
