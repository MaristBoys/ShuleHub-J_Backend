@Entity
@Table(name = "ref_room")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "room_name")
    private String roomName;
}