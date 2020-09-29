package com.paulograbin.confirmation.metrics.usecases.read;

import lombok.Data;

@Data
public class ReadMetricsResponse {
    public long activeUsers;
    public long totalUsers;
    public long usersThatAlreadyLoggedIn;
    public long totalEvents;
    public long publishedEvents;
    public long futureEvents;
    public long totalChapters;
    public long totalUserRequests;
    public long convertedUserRequest;
}
