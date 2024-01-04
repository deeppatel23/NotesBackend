package com.deep.notes.payload.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {

    public String title;

    public String content;
}
