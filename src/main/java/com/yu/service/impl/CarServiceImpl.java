package com.yu.service.impl;

import com.yu.service.CarService;
import com.yu.utils.CarUtils;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {
    public Integer speed = 100;

    @Override
    public boolean common(String key, int value) {
        try{
            CarUtils.client_set(key,value);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean init() {
        try {
            CarUtils.client_set(CarUtils.SPEED, speed);
            CarUtils.client_set(CarUtils.POWER,100);
            CarUtils.client_set(CarUtils.GEAR,0);
            CarUtils.client_set(CarUtils.MILEAGE,10050);
            CarUtils.client_set(CarUtils.ABS,0);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean sspeed() {
        try {
            speed-=40;
            CarUtils.client_set(CarUtils.SPEED, speed);
            CarUtils.client_set(CarUtils.BRAKINGWARING, 1);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean Stop() {
        try {
            speed = 0;
            CarUtils.client_set(CarUtils.SPEED, 0);
            CarUtils.client_set(CarUtils.BRAKINGWARING, 1);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean Speed(int speed) {
        try{
            this.speed = speed;
            CarUtils.client_set(CarUtils.SPEED,speed);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean Gear(int gear) {
        try{
            CarUtils.client_set(CarUtils.GEAR,gear);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean Mileage(int mileage) {
        try {
            CarUtils.client_set(CarUtils.MILEAGE, mileage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean Power(int power) {
        try{
            CarUtils.client_set(CarUtils.POWER,power);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean Light(int light) {
        try{
            CarUtils.client_set(CarUtils.LIGHT,light);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean Door(int door) {
        try {
            CarUtils.client_set(CarUtils.DOOR,door);
            return true;
        }catch (Exception e) {
            return false;
        }

    }
}
