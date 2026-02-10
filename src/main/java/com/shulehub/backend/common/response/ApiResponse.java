/*Per mantenere il backend professionale, ti suggerisco di creare una piccola classe "utility" in common.response.
 Questo garantisce che ogni risposta dal server abbia sempre lo stesso formato (utile per il frontend). */

package com.shulehub.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}