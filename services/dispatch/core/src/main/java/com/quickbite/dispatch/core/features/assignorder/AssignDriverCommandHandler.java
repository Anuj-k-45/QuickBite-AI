package com.quickbite.dispatch.core.features.assignorder;

import java.util.UUID;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.dispatch.core.data.RestaurantLocationRepository;
import com.quickbite.dispatch.core.model.RestaurantLocationReadModel;

@Component
public class AssignDriverCommandHandler implements ICommandHandler<AssignDriverCommand, UUID> {

    private final RestaurantLocationRepository restaurantLocationRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String DRIVER_GEO_KEY = "active:drivers";
    private static final double SEARCH_RADIUS_KM = 10.0; // 10 km search radius

    public AssignDriverCommandHandler(
            RestaurantLocationRepository restaurantLocationRepository,
            StringRedisTemplate redisTemplate) {
        this.restaurantLocationRepository = restaurantLocationRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UUID handle(AssignDriverCommand command) {
        // 1. Get restaurant coordinates from local MongoDB projection
        RestaurantLocationReadModel restaurant = restaurantLocationRepository.findById(command.restaurantId())
                .orElseThrow(() -> new RuntimeException(
                        "Restaurant location projection not found for ID: " + command.restaurantId()));

        // 2. Query Redis GEO to find nearby active drivers within a radius (e.g., 10km)
        Point restaurantPoint = new Point(restaurant.getLongitude(), restaurant.getLatitude());
        Metric distanceMetric = RedisGeoCommands.DistanceUnit.KILOMETERS;
        Circle range = new Circle(restaurantPoint, new Distance(SEARCH_RADIUS_KM, distanceMetric));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .sortAscending(); // Get the closest driver first!

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(DRIVER_GEO_KEY, range, args);

        if (results == null || results.getContent().isEmpty()) {
            throw new RuntimeException("No active drivers found within " + SEARCH_RADIUS_KM + "km of restaurant "
                    + command.restaurantId());
        }

        // 3. Pick the closest driver (first result since we sorted ascending)
        GeoResult<RedisGeoCommands.GeoLocation<String>> closestDriverResult = results.getContent().get(0);
        String driverIdStr = closestDriverResult.getContent().getName();
        double distanceKm = closestDriverResult.getDistance().getValue();

        System.out.println(
                "--- [DISPATCH] Found closest driver ID: " + driverIdStr + " at a distance of " + distanceKm + " km");

        return UUID.fromString(driverIdStr);
    }
}