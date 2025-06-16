package com.person.project.domain.model.bootcampmongo;

import com.person.project.domain.model.person.PersonBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BootcampPersonList {
    private Long idBootcamp;
    private List<PersonBasic> persons;
}
