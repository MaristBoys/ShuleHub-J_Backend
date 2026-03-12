package com.shulehub.backend.indicator_scale.model.dto;

// Rappresenta la scala di un indicatore (es. "Affollamento"), con i suoi range associati (es. "Basso", "Medio", "Alto")
// Includendo i ranges dentro IndicatorScaleDTO, il frontend scarica la configurazione una volta sola
//  e può generare i popover "Info" istantaneamente al passaggio del mouse o al tap oppure con il tasto info, 
// senza dover fare ulteriori chiamate al backend per recuperare i dettagli di ogni range


import lombok.Data;
import java.util.List;

@Data
public class IndicatorScaleDTO {
    private Short id;
    private String scaleName;
    private String indicatorType;
    private List<ScaleRangeDTO> ranges;

    @Data
    public static class ScaleRangeDTO {
        private String textValue;
        private Short minValue;
        private Short maxValue;
        private String attribute;
        private Short points;
    }
}
