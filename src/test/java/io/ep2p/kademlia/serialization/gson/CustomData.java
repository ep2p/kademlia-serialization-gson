package io.ep2p.kademlia.serialization.gson;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class CustomData implements Serializable {
    private String message;
}
