/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module TwseDbSave {
    requires JsonRequest;
    requires com.google.gson;
    requires com.google.errorprone.annotations;
    exports com.hunterhope.twsedbsave.service;
    exports com.hunterhope.twsedbsave.service.data;
    exports com.hunterhope.twsedbsave.service.exception;
    exports com.hunterhope.twsedbsave.dao;
    exports com.hunterhope.twsedbsave.entity;
    exports com.hunterhope.twsedbsave.dao.impl;
}
