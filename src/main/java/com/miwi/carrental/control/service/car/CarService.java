package com.miwi.carrental.control.service.car;

import com.miwi.carrental.control.dto.CarDto;
import com.miwi.carrental.control.exception.MyResourceNotFoundException;
import com.miwi.carrental.control.mapper.dto.CarDtoMapper;
import com.miwi.carrental.control.repository.CarDao;
import com.miwi.carrental.control.repository.CarDaoImpl;
import com.miwi.carrental.control.service.generic.GenericService;
import com.miwi.carrental.control.service.location.LocationService;
import com.miwi.carrental.models.entity.Car;
import com.miwi.carrental.models.enums.ECarStatus;
import com.miwi.carrental.models.enums.ERentalStatus;
import com.querydsl.core.types.Predicate;
import java.util.List;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarService extends GenericService<Car> {

  private Logger logger = LoggerFactory.getLogger(getClass().getName());

  private final CarDao carDao;
  private final CarDaoImpl carDaoImpl;
  private final CarDtoMapper carMapper;
  private final CarParameterService carParameterService;
  private final CarStatusService carStatusService;
  private final CarModelService carModelService;
  private final LocationService locationService;

  public CarService(final CarDao carDao,
      final CarDaoImpl carDaoImpl,
      final CarDtoMapper carMapper,
      final CarParameterService carParameterService,
      final CarStatusService carStatusService,
      final CarModelService carModelService,
      final LocationService locationService) {
    this.carDao = carDao;
    this.carDaoImpl = carDaoImpl;
    this.carMapper = carMapper;
    this.carParameterService = carParameterService;
    this.carStatusService = carStatusService;
    this.carModelService = carModelService;
    this.locationService = locationService;
  }

  public Page<Car> findByPredicate(Predicate predicate, Pageable pageable) {
    try {
      return checkFound(carDao.findAll(predicate, pageable));
    } catch (MyResourceNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "No cars found for predicate: " + predicate);
    }
  }

  public boolean checkIfRented(Long carId) {
    return carDaoImpl.findRentedCarByRentalId(carId).isPresent();
  }


  public Car changeToAvailable(Long carId, ECarStatus carStatus) {
    Car car = findById(carId);
    car.setCarStatus(carStatusService.findByCarStatusName(carStatus));

    return car;
  }

  public Car changeCarStatusByRentalStatus(Long carId, ERentalStatus eRentalStatus) {
    if (eRentalStatus == ERentalStatus.RENTED) {
      return changeToAvailable(carId, ECarStatus.UNAVAILABLE);
    }
    return changeToAvailable(carId, ECarStatus.AVAILABLE);
  }

  @Transactional
  public Car createNewCar(CarDto newCarDto) {
    Car car = carMapper.mapDtoToEntity(newCarDto);
    logger.debug("newCarDto has been mapped to entity");
    save(car);
    logger.info("New car for id {} has been created ", car.getId());
    return car;
  }

  public void editCar(Long id, CarDto carDto) {

    Car car = carDao.getOne(id);

    if (carDto.getRegistrationNumber() != null) {
      car.setRegistrationNumber(carDto.getRegistrationNumber());
    }
    if (carDto.getCarModelDto().getId() != null) {
      car.setCarModel(carModelService.findById((carDto.getCarModelDto().getId())));
    }
    if (carDto.getCarStatus() != null) {
      car.setCarStatus(carStatusService
          .findByCarStatusName(ECarStatus.valueOf(carDto.getCarStatus().toUpperCase())));
    }
    if (carDto.getLocationDto().getId() != null) {
      car.setLocation(locationService.findById(carDto.getLocationDto().getId()));
    }
    car.setCarParameter(carParameterService.editCarParameterByCarDto(carDto.getCarParameterDto()));
    save(car);
  }

  @Override
  public Car findById(Long id) {
    try {
      return checkFound(carDao.findById(id));
    } catch (MyResourceNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "The car with id: " + id + " was not found");
    }
  }

  @Override
  public Page<Car> findAll(Pageable pageable) {
    try {
      return checkFound(carDao.findAll(pageable));
    } catch (MyResourceNotFoundException ex) {
      logger.warn("Cars page is empty");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page with cars not found", ex);
    }
  }

  @Override
  public List<Car> findAll() {
    return carDao.findAll();
  }

  @Override
  public Car save(Car entity) {
    return carDao.save(entity);
  }

  @Override
  public void deleteById(Long id) {
    carDao.delete(findById(id));
  }
}
