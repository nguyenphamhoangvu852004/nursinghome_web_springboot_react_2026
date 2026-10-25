package com.eldercare.modules.careplan_management.careplan_design.service.v2;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UtilsTest {

    private CarePlanService cps;

    @BeforeEach
    void setup(){
        cps = new CarePlanService(
               null,null,null,null,null,null
        );
    }

    @Test
    void print_name_and_dataType_of_fields_in_object(){
        record Product(Long id, String name){}
        cps.checkValidDates(new Product(1l,"Nguyen Vu"));

    }
}


