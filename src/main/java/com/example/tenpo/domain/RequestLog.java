package com.example.tenpo.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="request_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String httpMethod;

    private String resourceUri;

    private String queryParams;

    @Column(length=5000)
    private String requestBody;

    @Column(length=5000)
    private String responseBody;

    private String principal;

    private LocalDateTime created;

    private Long elapsedTime;

    private Integer responseStatus;

    @Override
    public String toString() {
        return "RequestLog{" +
                "id=" + id +
                ", httpMethod='" + httpMethod + '\'' +
                ", resourceUri='" + resourceUri + '\'' +
                ", queryParams='" + queryParams + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", responseBody='" + responseBody + '\'' +
                ", principal='" + principal + '\'' +
                ", created=" + created +
                ", elapsedTime=" + elapsedTime +
                ", responseStatus=" + responseStatus +
                '}';
    }
}
