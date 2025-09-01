package sharve.sharvebot.message.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import sharve.sharvebot.message.type.AllMessage;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageResponseDTO implements Serializable {
        String status;
        Integer retcode;
        AllMessage data;
        String message;
        String wording;
        String echo;

        public AllMessage getData() {
                return data;
        }

        public void setData(AllMessage data) {
                this.data = data;
        }

        public String getEcho() {
                return echo;
        }

        public void setEcho(String echo) {
                this.echo = echo;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public Integer getRetcode() {
                return retcode;
        }

        public void setRetcode(Integer retcode) {
                this.retcode = retcode;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public String getWording() {
                return wording;
        }

        public void setWording(String wording) {
                this.wording = wording;
        }
}
