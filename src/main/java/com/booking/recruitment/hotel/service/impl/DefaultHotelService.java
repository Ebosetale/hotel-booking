package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.model.City;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.CityRepository;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class DefaultHotelService implements HotelService {
  private final HotelRepository hotelRepository;
  private final CityRepository cityRepository;

  @Autowired
  DefaultHotelService(HotelRepository hotelRepository, CityRepository cityRepository) {

    this.hotelRepository = hotelRepository;
    this.cityRepository = cityRepository;
  }

  @Override
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @Override
  public List<Hotel> getHotelsByCity(Long cityId) {
    return hotelRepository.findAll().stream()
        .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Hotel> searchHotelByDistance(Long cityId, String sortBy) {

    List<Hotel> hotels = null;
    City city = cityRepository.findById(cityId).orElse(null);

    if(city != null ){

      if(sortBy != null){
        double dRange = calculateDistance(city.getCityCentreLatitude(), city.getCityCentreLongitude());
        hotels =  hotelRepository.findAll().stream()
                .filter(h -> calculateDistance(h.getLatitude(), h.getLongitude()) >= dRange-1 )
                .limit(3)
                .collect(Collectors.toList());
      }
      else{
        hotels = hotelRepository.findAll().stream()
                .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
                .limit(3)
                .collect(Collectors.toList());
      }

    }

    return  hotels;
  }

  @Override
  public Hotel getHotelById(Long hotelId) {
    return hotelRepository.findById(hotelId).orElse(null);
  }

  @Override
  public Hotel deleteHotelById(Long hotelId) {
    Hotel toDelete = hotelRepository.findById(hotelId).orElse(null);
    if(toDelete != null){
      hotelRepository.delete(toDelete);
    }
    return toDelete;
  }


  @Override
  public Hotel createNewHotel(Hotel hotel) {
    if (hotel.getId() != null) {
      throw new BadRequestException("The ID must not be provided when creating a new Hotel");
    }

    return hotelRepository.save(hotel);
  }

  @Override
  public double calculateDistance(double lat, double lon) {

    //let's use +-5 lat and lon proximity
    double lat1 = lat + 5;
    double lat2 = lat - 5;
    double lon1 = lon + 5;
    double lon2 = lon -5;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    // convert to radians
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    // apply formulae
    double a = Math.pow(Math.sin(dLat / 2), 2) +
            Math.pow(Math.sin(dLon / 2), 2) *
                    Math.cos(lat1) *
                    Math.cos(lat2);
    double rad = 6371;
    double c = 2 * Math.asin(Math.sqrt(a));
    return rad * c;

  }
}
