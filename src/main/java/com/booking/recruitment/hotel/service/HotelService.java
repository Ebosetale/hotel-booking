package com.booking.recruitment.hotel.service;

import com.booking.recruitment.hotel.model.Hotel;

import java.util.List;

public interface HotelService {
  List<Hotel> getAllHotels();

  List<Hotel> getHotelsByCity(Long cityId);
  List<Hotel> searchHotelByDistance(Long cityId, String sortBy);
  Hotel getHotelById(Long hotelId);
  Hotel deleteHotelById(Long hotelId);


  Hotel createNewHotel(Hotel hotel);

  double calculateDistance(double lat, double longitude);
}
