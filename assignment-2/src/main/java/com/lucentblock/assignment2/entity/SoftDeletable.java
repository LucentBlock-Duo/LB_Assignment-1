package com.lucentblock.assignment2.entity;

import java.time.LocalDateTime;

public interface SoftDeletable {
   void setDeletedAt(LocalDateTime deletedAt);
    default void delete(){setDeletedAt(LocalDateTime.now());}
}
