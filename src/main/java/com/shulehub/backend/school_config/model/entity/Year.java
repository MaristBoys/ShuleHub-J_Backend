@Entity
@Table(name = "ref_year")
@Data
public class Year {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "year_description")
    private String yearDescription;

    @Column(name = "year_is_active")
    private boolean yearIsActive;
}
