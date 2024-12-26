/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module TwseDbSave {
    requires JsonRequest;
    requires com.google.gson;
    requires java.sql;
    requires com.google.errorprone.annotations;
    exports com.hunterhope.twsedbsave.service;
    exports com.hunterhope.twsedbsave.service.exception;
    exports com.hunterhope.twsedbsave.service.data to TwseDbSaveTest;    
    exports com.hunterhope.twsedbsave.dao to TwseDbSaveTest;
    exports com.hunterhope.twsedbsave.entity to TwseDbSaveTest;
    exports com.hunterhope.twsedbsave.dao.impl to TwseDbSaveTest;
    exports com.hunterhope.twsedbsave.other to TwseDbSaveTest;
}
