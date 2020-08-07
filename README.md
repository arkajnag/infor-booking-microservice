# infor-booking-microservice
Microservice designed using Spring Boot-Embedded H2 DB to create and handle Booking Related Services.

- Creating new Car Booking based on satisfactory search of existing user and car.
- Search and Retrieve Booking Records based on Search Criteria or without any criteria.
- Generate Reports

Different endpoints being exposed are:
Create New Booking: http://localhost:3003/rest/booking/new/booking


Search All Bookings: http://localhost:3003/rest/booking/all


Search Bookings by Dates: http://localhost:3003/rest/booking/search
[Query Parameters: bookingStartDate, bookingEndDate]


Report Total Payment of Bookings within Dates: http://localhost:3003/rest/booking/total/payment
[Query Parameters: bookingStartDate, bookingEndDate]

Report Total Bookings per hour within Dates: http://localhost:3003/rest/booking/booked/perHour
[Query Parameters: bookingStartDate, bookingEndDate]


Generate Report of All Bookings: http://localhost:3003/rest/booking/all/reports/{reportType}
