package com.aalifadesigns.matatatraveller.service;

import com.aalifadesigns.matatatraveller.dao.CategoryDao;
import com.aalifadesigns.matatatraveller.dao.entities.AttractionEntity;
import com.aalifadesigns.matatatraveller.dao.entities.CategoryEntity;
import com.aalifadesigns.matatatraveller.dao.entities.CityEntity;
import com.aalifadesigns.matatatraveller.dao.entities.ThreadEntity;
import com.aalifadesigns.matatatraveller.model.AttractionDto;
import com.aalifadesigns.matatatraveller.model.CategoryDto;
import com.aalifadesigns.matatatraveller.model.CityDto;
import com.aalifadesigns.matatatraveller.model.ThreadDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    CategoryDao categoryDao;

    @Autowired //Constructor Dependency Injection
    public CategoryServiceImpl(CategoryDao categoryDao) {

        this.categoryDao = categoryDao;
    }

    @Override
    public List<CategoryDto> fetchAllCategories() {
        //call findAll(), which will return a collection of entities, stored in allCategoryEntity
        List<CategoryEntity> allCategoryEntity = categoryDao.findAll();

        //copy the entities into CategoryDto objects and store them in a collection(which the method will return)
        List<CategoryDto> allCategoryDto = new ArrayList<CategoryDto>();

        //traverse the collection, using a forEach loop
        allCategoryEntity.forEach((eachCategoryEntity) -> {
            //copy each entity into a CategoryDto
            CategoryDto eachCategoryDto = new CategoryDto();
            BeanUtils.copyProperties(eachCategoryEntity, eachCategoryDto);

            //copy also the lists of threads (many-to-many mapped relationships)
            //traverse the collection,copy each entity into a Dto object, and form a collection of ThreadDto, to be set in the CategoryDto
            List<ThreadDto> allThreadDto = new ArrayList<ThreadDto>();
            for (ThreadEntity eachThreadEntity : eachCategoryEntity.getAllThreads()) {
                ThreadDto eachThreadDto = new ThreadDto();
                //copy each ThreadEntity into ThreadDto object
                BeanUtils.copyProperties(eachThreadEntity, eachThreadDto);
                //add the ThreadDto to the collection
                allThreadDto.add(eachThreadDto);
            }

            //set the ThreadDto collection inside CategoryDto
            eachCategoryDto.setAllThreads(allThreadDto);

            //add each CategoryDto object (containing now also the Threads collection) to the collection
            allCategoryDto.add(eachCategoryDto);
        });

        return allCategoryDto;
    }

    @Override
    public CategoryDto fetchACategory(int categoryId) {
        //call findById(), which returns an Optional<Entity> type
        Optional<CategoryEntity> optionalCategoryEntity = categoryDao.findById(categoryId);
        // if data exists, copy the entity to a corresponding CategoryDTO object
        CategoryDto categoryDto = null;
        if (optionalCategoryEntity.isPresent()) {
            categoryDto = new CategoryDto();
            BeanUtils.copyProperties(optionalCategoryEntity, categoryDto);

            //copy also the collection of threads (many-to-many mapped relationships)
            //traverse the collection,copy each entity into a Dto object, and form a collection of ThreadDto to be set in each CategoryDto
            List<ThreadDto> allThreadDto = new ArrayList<ThreadDto>();
            for (ThreadEntity eachThreadEntity : optionalCategoryEntity.get().getAllThreads()) {
                ThreadDto eachThreadDto = new ThreadDto();
                //copy each ThreadEntity inti ThreadDto object
                BeanUtils.copyProperties(eachThreadEntity, eachThreadDto);
                //add the ThreadDto to the collection
                allThreadDto.add(eachThreadDto);

            }
            //set the ThreadDto collection inside categoryDto object
            categoryDto.setAllThreads(allThreadDto);
        }
        return categoryDto;
    }

    @Override
    public CategoryDto addCategory(CategoryDto newCategory) {

        //copy the CategoryDto object into an entity
        CategoryEntity newCategoryEntity = new CategoryEntity();
        BeanUtils.copyProperties(newCategory, newCategoryEntity);

        //make use of saneAndFlush in-built method
        CategoryEntity savedCategoryEntity = categoryDao.saveAndFlush(newCategoryEntity);

        // set the type id in the new dto object
        newCategory.setCategoryId(savedCategoryEntity.getCategoryId());

        //return the Dto (now containing the Id)
        return newCategory;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto updateCategory) {
        return null;
    }

    @Override
    public void removeCategory(int categoryId) {

    }
}