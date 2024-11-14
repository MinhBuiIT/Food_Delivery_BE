package com.dev.dto.response;

import com.dev.models.Address;
import com.dev.models.ContactInfo;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class PaginationResponse {
    Object content;
    int currentPage;
    int totalPages;
    long totalElements;
}
