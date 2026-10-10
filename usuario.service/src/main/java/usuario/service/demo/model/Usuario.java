package usuario.service.demo.model;


import jakarta.persistence.*;

@Entity(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false,unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario(String email, String password, Rol rol)
    {
        this.email=email;
        this.password=password;
        this.rol=rol;
    }

    public Usuario() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public enum Rol{
        USER
    }
}
