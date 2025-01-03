
package com.example.tabelog.entity;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "stores")
@Data
@ToString(exclude = {"categoriesStores", "reviews", "reservations", "favorites"})
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image_name")
	private String imageName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "price_min")
	private Integer priceMin;
	
	@Column(name = "price_max")
	private Integer priceMax;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@Column(name = "opening_time")
	private LocalTime openingTime;
	
	@Column(name = "closing_time")
	private LocalTime closingTime;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "regular_holiday")
	private String regularHoliday;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	@OneToMany(mappedBy = "store", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id ASC")
    private List<CategoryStore> categoriesStores; 
	
	@OneToMany(mappedBy = "store", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Review> reviews;
	
	@OneToMany(mappedBy = "store", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;
	
	@OneToMany(mappedBy = "store", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Favorite> favorites;
	
	// 平均評価を取得する
    @Transient
    public Double getAverageRating() {
        Double averageRating = reviews.stream()
                                     .mapToInt(Review::getRating)
                                     .average()
                                     .orElse(0.0);

        return averageRating;
    }
}
