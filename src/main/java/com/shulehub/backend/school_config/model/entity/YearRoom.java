@Entity
@Table(name = "cfg_year_room")
@Data
public class YearRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_year")
    private Short idYear;

    @Column(name = "id_room")
    private Short idRoom;
}
