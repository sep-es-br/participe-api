package br.gov.es.participe.util.dto;

import org.springframework.social.facebook.api.User;

import java.util.Map;
import java.util.Objects;

public class UserDto {

    private String firstName;

    private String lastName;

    private String imageUrl;

    private String email;

    public UserDto(String firstName, String lastName, String imageUrl, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public UserDto(User user) {
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setImageUrl(user);
        setEmail(user.getEmail());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageUrl(User user) {
        if (user.getCover() != null) {
            setImageUrl(user.getCover().getSource());
        } else {
            Map<String, Object> pictureObj = (Map<String, Object>) user.getExtraData().get("picture");
            if (pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj.containsKey("url")) {
                    setImageUrl(dataObj.get("url").toString());
                }
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(firstName, userDto.firstName) &&
                Objects.equals(lastName, userDto.lastName) &&
                Objects.equals(imageUrl, userDto.imageUrl) &&
                Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, imageUrl, email);
    }
}
